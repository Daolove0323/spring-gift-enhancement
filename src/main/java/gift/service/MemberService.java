package gift.service;

import static gift.controller.member.MemberMapper.toMemberResponse;

import gift.controller.member.MemberMapper;
import gift.controller.member.MemberRequest;
import gift.controller.member.MemberResponse;
import gift.controller.member.SignUpRequest;
import gift.domain.Member;
import gift.exception.FailedHashException;
import gift.exception.MemberAlreadyExistsException;
import gift.exception.MemberNotExistsException;
import gift.repository.MemberRepository;
import gift.util.HashUtil;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Page<MemberResponse> findAll(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        List<MemberResponse> memberResponses = memberPage.stream()
            .map(MemberMapper::toMemberResponse).toList();
        return new PageImpl<>(memberResponses, pageable, memberPage.getTotalElements());
    }

    public MemberResponse findById(UUID id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotExistsException::new);
        return toMemberResponse(member);
    }

    public MemberResponse findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotExistsException::new);
        return toMemberResponse(member);
    }

    public MemberResponse save(SignUpRequest member) {
        memberRepository.findByEmail(member.getEmail()).ifPresent(p -> {
            throw new MemberAlreadyExistsException();
        });
        try {
            System.out.println("사인업리퀘스트멤버: " + member.getPassword());
            member.setPassword(HashUtil.hashPassword(member.getPassword()));
        } catch (Exception e) {
            throw new FailedHashException();
        }
        return toMemberResponse(memberRepository.save(MemberMapper.from(member)));
    }

    public MemberResponse update(UUID id, MemberRequest member) {
        Member target = memberRepository.findById(id).orElseThrow(MemberNotExistsException::new);
        target.setMember(member);
        return toMemberResponse(target);
    }

    public void delete(UUID id) {
        memberRepository.findById(id).orElseThrow(MemberNotExistsException::new);
        memberRepository.deleteById(id);
    }
}